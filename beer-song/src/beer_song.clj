(ns beer-song)

(defn- bottles [num]
  (case num
    0 "no more bottles"
    1 "1 bottle"
    -1 "99 bottles"
    (format "%s bottles" num)))

(defn- command [num]
  (case num
    0 "Go to the store and buy some more"
    1 "Take it down and pass it around"
    "Take one down and pass it around"))

(defn verse
  "Returns the nth verse of the song."
  [num]
  (format "%s of beer on the wall, %s of beer.\n%s, %s of beer on the wall.\n"
          (clojure.string/capitalize (bottles num))
          (bottles num)
          (command num)
          (bottles (- num 1))))

(defn sing
  "Given a start and an optional end, returns all verses in this interval. If
  end is not given, the whole song from start is sung."
  ([start]
   (sing start 0))
  ([start end]
   (clojure.string/join "\n" (map verse (range start (- end 1) -1)))))
