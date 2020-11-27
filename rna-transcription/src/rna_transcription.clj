(ns rna-transcription)

;; My first version uses a hash map:

;; (def nucleotides {\G \C,
;;                   \C \G,
;;                   \T \A,
;;                   \A \U})

;; (defn nucl-compl [n]
;;   (assert (contains? nucleotides n))
;;   (get nucleotides n))

;; (defn to-rna [dna]
;;   (apply str (map nucl-compl dna)))

;; Second version uses `case`:

(defn- nucl-compl [n]
  {:pre [(some #{n} [\G \C \T \A])]}
  (case n
    \G \C
    \C \G
    \T \A
    \A \U))

(defn to-rna [dna]
  (apply str (map nucl-compl dna)))


;; Things to remember:

;; `contains?` doesn't work on lists, since it checks for the presence of a key.
;; `some` is the idiomatic way to check for list membership. See
;; https://stackoverflow.com/questions/3249334/test-whether-a-list-contains-a-specific-value-in-clojure
;; and https://stackoverflow.com/questions/27595787/why-can-i-use-a-set-as-predicate-in-clojure

;; Sets (and hash maps) can be used as functions of one argument. If the
;; argument is a member of the set, it is returned, otherwise `nil` is returned.
;; (In case of a hash map, the argument is a key and the key's value is returned.)

;; `assert` can be used to make assertions, but doing it with a pre-condition is preferable.

;; `defn-` creates private functions. There is no equivalent for `def`, you need
;; to write `(def ^{:private true} <name> ...)'
