(ns two-fer)

(defn two-fer
  ([] "One for you, one for me.")
  ([name] (format "One for %s, one for me." name)))

;; Things to remember:

;; - In multi-arity functions, the function bodies should be in ascending order.
