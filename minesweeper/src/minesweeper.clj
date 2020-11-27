(ns minesweeper
  [:require [clojure.string :as s]])

;; Taken from the test file.
(def line-separator (System/getProperty "line.separator"))

(defn on-board? [[width height] [x y]]
  (and (< -1 x width) ; -1 because 0 is a valid board position.
       (< -1 y height)))

(defn list-adjacents
  "Return a list of the cells surrounding `coor`.
  `coor` is a coordinate pair on the board (starting with [0 0]). `board-size`
  is a seq of the form [width height]."
  [coor board-size]
  (let [seq-adder (fn [coll] ; Take a coll and return a function that adds a seq to coll element-wise.
                    (partial map + coll))
        offsets (for [x (range -1 2)
                      y (range -1 2)
                      :when (not= 0 x y)]
                  [x y])]
    (filter (partial on-board? board-size) (map (seq-adder coor) offsets))))

;; The original string representation is not very useful, because it's difficult
;; to find the cells surrounding a particular cell and it's difficult to update
;; a cell's contents. With a hash-map, both are considerably easier.

(defn string->board
  "Convert a board from a string into a hash-map mapping coordinates to cell content.
  Add the size of the board as metadata in the form [width height]."
  [string]
  (letfn [(process-line  [n line]
            (map-indexed #(vector [%1 n] %2) line))]
    (let [board (s/split-lines string)
          width (count (first board))
          height (count board)]
      (with-meta (reduce into {} (map-indexed #(process-line %1 %2) board)) {:size [width height]}))))

(defn count-mines
  "Return the number of mines surrounding `cell` in `board`."
  [cell board]
  (->> (list-adjacents cell (:size (meta board)))
       (filter #(= (get board %) \*))
       count))

(defn update-cell
  "Count the mines surrounding `cell` and update `board` accordingly.
  Return the updated board."
  [board cell]
  (into board {cell (count-mines cell board)}))

(defn draw-cell
  "Draw a single cell.
  If the cell content is 0 (meaning no surrounding mines), convert it to a
  space. Any other cell content is returned unchanged."
  [c]
  (if (= c 0) \space c))

(defn board->string
  "Convert a board into a string representation."
  [board]
  (letfn [(draw-line [n board]
            (apply str (map #(draw-cell (get board [% n])) (range 0 (first (:size (meta board)))))))]
    (s/join line-separator (map #(draw-line % board) (range 0 (second (:size (meta board))))))))

(defn draw [string]
  (let [board (string->board string)]
    (board->string (reduce update-cell board (filter #(= (get board %) \space) (keys board))))))


;; Things to remember

;; Find a good way to represent the data, it's half the solution.

;; `for` is used for list comprehension. See <https://clojuredocs.org/clojure.core/for>.
