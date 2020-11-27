(ns collatz-conjecture)

(defn collatz [num]
  {:pre [(int? num) (pos? num)]}
  (loop [steps 0
         n num]
    (if (= n 1)
      steps
      (recur (+ 1 steps)
             (if (odd? n)
               (+ (* 3 n) 1)
               (/ n 2))))))
