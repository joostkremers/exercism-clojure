(ns run-length-encoding)

;; First version:

;; (defn- compress-consec [[consec first-char]]
;;   (str (if (= (count consec) 1) ""
;;            (count consec))
;;        first-char))

;; (defn run-length-encode
;;   "encodes a string with run-length-encoding"
;;   [plain-text]
;;   (clojure.string/replace plain-text #"(.)\1*" compress-consec))

;; But if `clojure.string/replace` only looks for characters that repeat at
;; least once, `compress-consec` can of course be simplified:

(defn- compress-consec [[consec first-char]]
  (str (count consec) first-char))

(defn run-length-encode
  "encodes a string with run-length-encoding"
  [plain-text]
  (clojure.string/replace plain-text #"(.)\1+" compress-consec))

(defn- uncompress-consec [[_ n c]]
  (apply str (repeat (Integer/parseInt n) c)))

(defn run-length-decode
  "decodes a run-length-encoded string"
  [cipher-text]
  (clojure.string/replace cipher-text #"([0-9]+)([A-Za-z\s])" uncompress-consec))


;; Things to remember:

;; `clojure.string/replace` can take a function as replacement. This function is
;; passed a vector of all groups in the match, beginning with group 0 (i.e., the
;; entire match).

