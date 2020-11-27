(ns anagram
  [:require [clojure.string :as string]])

(defn anagram? [word anagram]
  (let [w (string/lower-case word)
        a (string/lower-case anagram)]
    (and (not (= w a))
         (= (sort w) (sort a)))))

(defn anagrams-for [word prospect-list]
  (filter (partial anagram? word) prospect-list))

;; Things to remember:

;; `partial` can create partial functions from a function name and any number of
;; arguments (though fewer than the number of arguments the function takes,
;; obviously).
