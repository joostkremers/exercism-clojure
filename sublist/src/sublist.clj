(ns sublist)

(defn sublist?
  "Return `true` if `list1` is a sublist of `list2`"
  [list1 list2]
  (let [len (count list1)]
    (loop [l list2]
      (cond
        (> len (count l)) false
        (= list1 (subvec l 0 len)) true
        :else (recur (subvec l 1))))))

(defn classify [list1 list2]
  (cond
    (= list1 list2) :equal
    (= (count list1) (count list2)) :unequal
    (sublist? list1 list2) :sublist
    (sublist? list2 list1) :superlist
    :else :unequal))

