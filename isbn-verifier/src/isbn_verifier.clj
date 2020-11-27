(ns isbn-verifier)

(defn- char-to-int [c]
  (if (= c \X)
    10
    (Character/digit c 10)))

(defn isbn? [isbn]
  (let [digits (clojure.string/replace isbn "-" "")]
    (if (not (re-matches #"[0-9]{9}[0-9X]" digits))
      false
      (let [sum (reduce + (map-indexed #(* (- 10 %1) (char-to-int %2)) digits))]
        (= (mod sum 11) 0)))))

;; Perhaps using `->>` makes things a bit clearer:

;; (defn isbn? [isbn]
;;   (let [digits (clojure.string/replace isbn "-" "")]
;;     (if (not (re-matches #"[0-9]{9}[0-9X]" digits))
;;       false
;;       (let [sum (->> digits
;;                      (map char-to-int)
;;                      (map-indexed #(* (- 10 %1) %2))
;;                      (reduce +))]
;;         (= (mod sum 11) 0)))))


;; Things to remember:

;; `re-matches` can be used to test if a string matches a regexp. The regexp
;; should match the *whole* string, not just a substring.

;; `if` returns `nil` if the condition is false and there is no `else` clause.
;; Since the tests call for a `false` return value, it has to be returned
;; explicitly here. (I negated the `re-matches` clause in the `if` because I
;; don't like a short "else" block after a longish "then" block. Plus, it better
;; fits my train of thought.)

;; Rewrite suggested by the Exercism mentor:

;; (if-not (re-matches #"[0-9]{9}[0-9X]" digits)
;;   false
;;   (let [div-11? #(zero? (mod % 11))]
;;     (->> digits
;;          (map char->int)
;;          (map * (range 10 0 -1))
;;          (reduce +)
;;          div-11?)))

;; Threading macros can make the code clearer.

;; Naming convention: `char->int` instead of `char-to-int`.

;; `map-indexed` looks like a neat trick, but in this case it makes the intent
;; of the code less clear, because it's not immediately clear what the anonymous
;; function does. `range` works better here.

;; `if-not` instead of `(if (not ...`.

;; You can `let`-bind symbols to anonymous functions.
