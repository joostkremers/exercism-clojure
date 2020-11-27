(ns say
  [:require [clojure.string :as s]])

(def base-numbers {0 "" ; Empty string because 1000 is not "one thousand zero".
                   1 "one"
                   2 "two"
                   3 "three"
                   4 "four"
                   5 "five"
                   6 "six"
                   7 "seven"
                   8 "eight"
                   9 "nine"
                   10 "ten"
                   11 "eleven"
                   12 "twelve"
                   13 "thirteen"
                   14 "fourteen"
                   15 "fifteen"
                   16 "sixteen"
                   17 "seventeen"
                   18 "eighteen"
                   19 "nineteen"
                   20 "twenty"
                   30 "thirty"
                   40 "forty"
                   50 "fifty"
                   60 "sixty"
                   70 "seventy"
                   80 "eighty"
                   90 "ninety"})

(defn chunk-thousands
  "Break `num` into chunks of thousands.
  A number such as 1234567 is broken up into (1 234 567)."
  [num]
  (loop [res []
         n num]
    (if (= n 0)
      res
      (recur (cons (mod n 1000) res) (quot n 1000)))))

;; `interleave-units` needs some tricks to function properly. First, the easiest
;; way to interleave the correct number of units is by working from the end of
;; the list, hence the two calls to `reverse`. Second, the list of units
;; contains `false`, because `interleave` stops when the shortest list is
;; exhausted. So the list of units needs to be at least as long as the list of
;; 1000-chunks. Third, `interleave` always inserts one unit too many (i.e.,
;; after the second `reverse`, we have ("thousand" 567) for the number 567), so
;; we need to remove the first element of the list.

(defn interleave-units
  "Break `num` into chunks of thousands and interleave units.
  A number such as 1234567 returns (1 \"million\" 234 \"thousand\" 567)"
  [num]
  (-> (chunk-thousands num) 
      reverse 
      (interleave ["thousand" "million" "billion" false])
      reverse
      rest))

(defn say-tens [n]
  (or (base-numbers n)
      (let [ones (mod n 10)
            tens (- n ones)]
        (str (base-numbers tens) "-" (base-numbers ones)))))

(defn say-hundreds [n]
  (let [hundreds (base-numbers (quot n 100))
        tens (say-tens (mod n 100))]
    (cond
      ;; Return only those parts that are not empty lists.
      (and (empty? hundreds) (empty? tens)) nil
      (empty? hundreds) (list tens)
      (empty? tens) (list hundreds "hundred")
      :else (list hundreds "hundred" tens))))

(defn say-list [lst]
  (loop [[n unit & rest]   lst
         result            []]
    (cond
      (nil? unit) (concat result (say-hundreds n))
      (= 0 n) (recur rest result) ; Ignore `0 "thousand"` in, e.g., (1 "million" 0 "thousand" 1).
      :else (recur rest (concat result (say-hundreds n) (list unit))))))

(defn number [num]
  (cond
    (= 0 num) "zero" ; This is a special case, since e.g., 1000 is not "one thousand zero".
    (or (< num 0) (>= num 1000000000000)) (throw (IllegalArgumentException. "Argument out of range"))
    :else (->> num
               interleave-units
               say-list
               (s/join " "))))
