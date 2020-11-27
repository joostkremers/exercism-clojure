(ns reverse-string)

(defn reverse-string [s]
  (apply str (into () s)))


;; Things to remember:

;; `str` can take multiple arguments: it converts them into strings and then
;;  concatenates them. This is why `apply` works here.

;; `conj` adds elements to the "optimal" side of a collection, i.e., to the end
;; for a vector but to the beginning for a list.

;; `into` uses `conj` (perhaps not literally, but in spirit), which is why the
;; first argument to `into` here should be an empty list, not a vector.
