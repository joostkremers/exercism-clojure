(ns series)

(defn slices [string length]
  (if (= length 0)
    [""]
    (let [maxlen (count string)]
      (loop [i 0
             res []]
        (if (> (+ i length) maxlen)
          res
          (recur (inc i) (conj res (subs string i (+ i length)))))))))

;; Things to remember:

;; There is a built-in function `partition` that can be used here:

;; (defn slices [string length]
;;   (if (zero? length)
;;     [""]
;;     (map #(apply str %) (partition length 1 string))))
