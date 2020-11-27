(ns word-count
  (:require [clojure.string :as string]))

(defn- tally-words [words]
  (loop [[w & ws] words
         tally {}]
    (if (nil? w)
      tally
      (recur ws (assoc tally w (inc (get tally w 0)))))))

(defn word-count [s]
  (let [extract-words #(string/split % #"[^a-zA-Z0-9]")]
    (->> s
         extract-words
         (remove empty?)
         (map string/lower-case)
         tally-words)))

;; Things to remember:

;; This doesn't work:

;; (->> s
;;      #(string/split % #"[^a-zA-Z0-9]")
;;      (remove empty?)
;;      (map string/lower-case))

;; The reason seems to be that it expands to this:

;; (map string/lower-case
;;      (remove empty? #(string/split % #"[^a-zA-Z0-9]")))

;; That's to say, the first expression (the anonymous function) isn't combined
;; with `s`. Not sure why, though. (But I guess that explains why in the
;; mentor's solution to the ISBN verifier problem there is a local function
;; `div-11?`.)

;; Following mentor comments:

;; The core function `frequencies` can be used instead of `tally-words`. It does
;; the same thing but uses a transient hash map internally.

;; `frequencies` uses `reduce` instead of `loop`/`recur`.

;; Transient data structures are interesting: https://clojure.org/reference/transients.

;; It is possible to use anonymous functions in threading macros, but they need
;; to be wrapped in an extra set of parameters:

;; (->> s
;;      (#(string/split % #"[^a-zA-Z0-9]"))
;;      (remove empty?)
;;      (map string/lower-case))

;; See
;; https://stackoverflow.com/questions/10740265/threading-macro-with-anonymous-functions
;; for details.

;; Alternatively, this is also possible, of course:

;; (->> (#(string/split % #"[^a-zA-Z0-9]"))
;;      (remove empty?)
;;      (map string/lower-case))

;; IOW, the first argument of `->>` can of course be a longer expression.

;; `clojure.string/split` is not lazy. `re-seq` can be used here instead, which
;; does return a lazy seq.

;; In Java regexes, the character class \w can be used instead of [a-zA-Z_0-9],
;; which, save for the underscore, is the same as my regex above.
