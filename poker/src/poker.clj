(ns poker
  [:require [clojure.string :as string]])

(def card-regexp #"(\A(?:[2-9AJQK]|10))([HDCS])\Z")

(defn rank [card]
  (nth (re-find card-regexp card) 1))

(defn suit [card]
  (nth (re-find card-regexp card) 2))

(defn rank->int [rank]
  (cond
    (= rank "A") 14
    (= rank "K") 13
    (= rank "Q") 12
    (= rank "J") 11
    :else (Integer/parseInt rank)))

(defn rank> [& args]
  (apply > (map rank->int args)))

(defn compare-ranks
  "Three-way comparator for two sorted lists of ranks.
  Return value is 0 if `ranks-x` and `ranks-y` are equal, negative if `ranks-x` is
  smaller than `ranks-y`, and positive if `ranks-x` is larger than `ranks-y`. If
  the lists are of unequal length, comparison stops when the shortest list is
  exhausted. If all ranks up to that point were equal, the two lists are
  considered equal."
  [ranks-x ranks-y]
  (loop [[x & xs] ranks-x
         [y & ys] ranks-y]
    (cond
      (or (nil? x) (nil? y)) 0
      (= x y) (recur xs ys)
      :else (compare (rank->int x) (rank->int y)))))

;; The following functions determine whether a given hand qualifies as a
;; particular poker hand (here called a category). The return value is either
;; `false`/`nil` or a number indicating the strength of the hand, with 9 the
;; strongest, 1 the weakest.

;; Note: these functions do *not* check if the hand also matches a higher-ranked
;; category. The proper way to categorize a hand is by using `categorize-hand`
;; below.

(declare straight? flush?)

(defn straight-flush? [hand]
  (and (straight? hand)
       (flush? hand)
       9))

(defn four-of-a-kind? [hand]
  (when (some #(= 4 (count %)) (vals (:ranks (meta hand))))
    8))

(defn full-house? [hand]
  (when (= 2 (count (:ranks (meta hand))))
    7))

(defn flush? [hand]
  (when (= 1 (count (:suits (meta hand))))
    6))

(defn straight? [hand]
  ;; To test whether a list of ranks is a straight, the list is converted to a
  ;; regex and tested against a sequence of ranks. The additional pattern
  ;; accounts for a 5-high straight, in which A counts as 1.
  (let [ranks-re (re-pattern (apply str (map rank hand)))]
    (when (re-find ranks-re "AKQJ1098765432 A5432")
      5)))

(defn three-of-a-kind? [hand]
  (when (some #(= 3 (count %)) (vals (:ranks (meta hand))))
    4))

(defn two-pair? [hand]
  (when (= 3 (count (:ranks (meta hand))))
    3))

(defn one-pair? [hand]
  (when (some #(= 2 (count %)) (vals (:ranks (meta hand))))
    2))

(defn high-card? [_hand]
  1)

(def categorize-hand (some-fn straight-flush?
                              four-of-a-kind?
                              full-house?
                              flush?
                              straight?
                              three-of-a-kind?
                              two-pair?
                              one-pair?
                              high-card?))

;; Helper functions for comparing hands of the same category (here called
;; secondary comparison). These functions are used to extract the ranks of the
;; relevant card groups.

(defn all-ranks
  "Return a list of the ranks of all cards in `hand`."
  ;; Since `hand` is sorted, we don't need to sort again.
  [hand]
  (map rank hand))

(defn straight
  "Return a list of the ranks of a straight.
  This is similar to `all-ranks`, except when `hand` is a 5-high straight, in
  which case the ace is converted to 1."
  [hand]
  (if (= (apply str (map rank hand)) "A5432")
    ["5" "4" "3" "2" "1"]
    (map rank hand)))

(defn quadruplet
  "Return the rank of the quadruplet in a four-of-a-kind as a list."
  [hand]
  (keys (filter #(= 4 (count (val %))) (:ranks (meta hand)))))

(defn triplet
  "Return the rank of the triplet in a three-of-a-kind as a list."
  [hand]
  (keys (filter #(= 3 (count (val %))) (:ranks (meta hand)))))

(defn pairs
  "Return a sorted list of the ranks of the pairs in `hand`."
  [hand]
  ;; Not sure if `sort` is necessary here, because the original hand is sorted,
  ;; but the keys are extracted from a hash map, so best to be sure.
  (sort rank> (keys (filter #(= 2 (count (val %))) (:ranks (meta hand))))))

(defn kickers
  "Return a sorted list of the ranks of the kickers in `hand`.
  The kickers are those cards that do not form pairs, triplets or quadruplets."
  [hand]
  (sort rank> (keys (filter #(= 1 (count (val %))) (:ranks (meta hand))))))

;; A hash map of secondary comparison functions. If an entry contains more than
;; one function, they are tried in the order given: if the first function yields
;; equality, the next one is tried, until the list is exhausted.

(def secondary-compare-fns {9 [straight]            ; straight flush
                            8 [quadruplet kickers]  ; four of a kind
                            7 [triplet pairs]       ; full-house
                            6 [all-ranks]           ; flush
                            5 [straight]            ; straight
                            4 [triplet kickers]     ; three-of-a-kind
                            3 [pairs kickers]       ; two-pair
                            2 [pairs kickers]       ; one-pair
                            1 [all-ranks]})         ; high-card

;; Applying these functions yields a list of ranks, which can then be compared
;; using `compare-ranks`. For example, collecting the comparators for "5H 5D 5S
;; 5C 10H", which is a four-of-a-kind, yields the list ("5" "10"): "5" is the
;; rank of the quadruplet, "10" the rank of the kicker. For "5H 10D 10S 10H
;; 10C", however, which is also a four-of-a-kind, the list of secondary
;; comparators is ("10" "5").

(defn collect-comparators
  "Collect the ranks used for secondary comparison.
  The return value is a list that is first sorted by priority, then by rank."
  [hand fns]
  (reduce #(concat %1 (%2 hand)) [] fns))

(defn compare-hands
  "Three-way comparator for two hands.
  Hands are first compared by their category (straight flush, four of a kind,
  etc.), represented as a number. Two hands of the same category are further
  compared by the ranks of their cards, as defined by `secondary-compare-fns`."
  [x y]
  (let [cat-x (:cat (meta x))
        cat-y (:cat (meta y))]
    (if-not (= cat-x cat-y)
      ;; x and y are reversed because we want to sort from high to low.
      (compare cat-y cat-x)
      (compare-ranks (:seccomp (meta y)) (:seccomp (meta x))))))

(defn hands= [x y]
  (= 0 (compare-hands x y)))

(defn parse-hand
  "Parse `hand`.
  The hand is converted to a list sorted by rank and the following metadata is
  added: the original string, groupings of the cards by rank and by suit, the
  category and the secondary comparators."
  [hand]
  ;; The metadata is added in two stages, because the groupings by rank and suit
  ;; are needed to determine the category and secondary comparators.
  (let [cards (sort-by rank rank> (string/split hand #"\s"))
        parsed-hand (with-meta cards {:ranks (group-by rank cards)
                                      :suits (group-by suit cards)
                                      :orig hand})
        cat (categorize-hand parsed-hand)
        seccomp (collect-comparators parsed-hand (secondary-compare-fns cat))]
    (vary-meta parsed-hand assoc :cat cat :seccomp seccomp)))

(defn best-hands [hands]
  (let [sorted-hands (->> hands
                          (map parse-hand)
                          (sort compare-hands))]
    (map #(:orig (meta %))
         (take-while #(hands= % (first sorted-hands)) sorted-hands))))


;;Things to remember:

;; `sort` (and other sorting functions) can take a `comparator` argument, which
;; must implement java.util.Comparator. The easiest way to do this is to use
;; `compare` to do the actual comparing, but that won't work if you need to
;; compare elements of different types (as is the case here).
;; See <https://clojure.org/guides/comparators> for details.


;; Notes:

;; As it stands, I should probably have `categorize-hand` return not just the
;; category but also the secondary comparators. As it stands, there doesn't seem
;; to be any reason to extract those separately. (In an earlier version of the
;; code, the secondary comparators weren't stored as metadata but extracted on
;; an as-needed basis in `compare-hands`.)
