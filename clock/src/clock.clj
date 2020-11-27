(ns clock)

(defn clock->string [[hh mm]]
  (format "%02d:%02d" hh mm))

(defn norm-minutes
  "Normalize `minutes`.
  Convert `minutes` into a vector of hours + remaining minutes. If `minutes` is
  negative, the number of hours in the return value is negative, the remaining
  minutes are subtracted from 60 and the number of hours is corrected; i.e., if
  minutes = -2, the return value is [-1 58]."
  [minutes]
  (let [mm (rem minutes 60)]
    (if (< minutes 0)
      (vector (- (quot minutes 60) 1)
              (+ 60 mm))
      (vector (quot minutes 60)
              mm))))

(defn clock
  "Normalize a clock value.
  The number of `hours` is converted to a value between 0-24, the number of
  `minutes` to a value between 0-60. If `minutes` is larger than 59, add the
  corresponding number of hours to `hours` before conversion. Negative values
  are handled correctly; i.e., a clock value of [5 -2] is converted to [4 58]."
  [hours minutes]
  (let [[roll-over mm] (norm-minutes minutes)
        hh (-> roll-over
               (+ hours)
               (rem 24))]
    (vector (if (<= 0 hh) hh (+ hh 24)) mm)))

(defn add-time
  "Add minutes to a time value.
  `[hh mm] is a time representation returned by `clock`. `time` a time value in
  minutes. Return the time after adding `minutes` to `[hh mm]`. `time` may be a
  negative value."
  [[hh mm] time]
  (clock 0 (+ (* 60 hh) mm time)))
