(ns armstrong-numbers)

;; I need an exponentiation function, which Clojure doesn't seem to have. So I got this
;; from https://stackoverflow.com/questions/5057047/how-to-do-exponentiation-in-clojure:
(defn exp [x n]
  (reduce * (repeat n x)))

;; My first attempt doesn't handle the 17-digit number from the test-suite, as
;; it blows the stack. (I'm not sure why, though. Wasn't `loop/recur` supposed to
;; be TCO'd?)

;; (defn armstrong? [num]
;;   (let [n-digits (count (str num))
;;         res (loop [sum 0
;;                    n num]
;;               (if (= n 0)
;;                 sum
;;                 (recur (+ sum (exp (mod n 10) n-digits))
;;                        (int (/ n 10)))))]
;;     (= res num)))

;; Recap: Boy, I should really read my error messages... The problem isn't the
;; stack, it's the conversion to `int`. The following works:

;; (defn armstrong? [num]
;;   (let [n-digits (count (str num))
;;         res (loop [sum 0
;;                    n num]
;;               (if (= n 0)
;;                 sum
;;                 (recur (+ sum (exp (mod n 10) n-digits))
;;                        (quot n 10))))]
;;     (= res num)))

;; But doing it in a functional style is more typical Clojure:

(defn armstrong? [num]
  (let [num-str (str num)
        n-digits (count num-str)
        res (reduce + (map #(exp (Character/digit % 10) n-digits) num-str))]
    (= res num)))


;; Things to remember:

;; - Strings in Clojure are `seq`able.

;; - The length of a seq is given by `count`, not `length` or `len` or something
;;   similar.

;; - `let` in Clojure is like `let*` in Elisp/Common Lisp/Scheme.

;; - Whenever you feel the need to use `loop/recur`, consider whether some
;;   combination of `map/reduce/filter` might do the trick.

;; - Read error messages!
