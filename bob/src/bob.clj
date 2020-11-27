(ns bob)

(defn empty-response? [s]
  (empty? (clojure.string/replace s #"[ \s]+" "")))

(defn question? [s]
  (re-find #"\?[ \s]*\z" s))

(defn shouted? [s]
  (and (re-find #"[A-Za-z]" s)
       (= s (clojure.string/upper-case s))))

(defn response-for [s]
  (cond
    (empty-response? s) "Fine. Be that way!"
    (shouted? s) (if (question? s)
                   "Calm down, I know what I'm doing!"
                   "Whoa, chill out!")
    (question? s) "Sure."
    :else "Whatever."))


;; Things to remember:

;; Regexes are created with `re-pattern` or the special syntax #"abc".

;; `cond` doesn't wrap each condition-statement pair in parens.
