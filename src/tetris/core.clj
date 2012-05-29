(ns tetris.core
  (:use quil.core))

(def cols 10)

(def game-width-pixels 400)
(def game-height-pixels 600)

(def block-width (int (/ game-width-pixels cols)))

;(defn rotate90 [model] (map (fn [[x y]] [-y x]) model))

(defn block [x y]
  (rect (* block-width x) (- game-height-pixels (* block-width y)) block-width block-width)
  )

(def rows (int (/ game-height-pixels block-width)))

(def shapes
  {:i [:maroon [0 0] [1 0] [2 0] [3 0]]
   :j [:white [0 0] [1 0] [2 0] [2 1]]
   :l [:magenta [0 0] [0 1] [1 0] [2 0]]
   :o [:dark-blue [0 0] [0 1] [1 0] [1 1]]
   :s [:green [0 1] [1 1] [1 0] [2 0]]
   :t [:brown [0 0] [1 0] [2 0] [1 1]]
   :z [:cyan [0 0] [0 1] [1 1] [2 1]]})

(defn draw-tetrimino [{piece :piece x0 :x y0 :y}]
  (dorun
   (let [[color & coords] (shapes piece)]
     (apply fill (get {:maroon [128 0 0]
                      :white [255 255 255]
                      :magenta [255 0 255]
                      :dark-blue [0 0 120]
                      :green [0 200 0]
                      :brown [255 100 0]
                      :cyan [0 255 255]
                      } color))
     (for [[x y] coords]
       (block (+ x x0) (+ y y0))))))

  
(def current (ref {:piece :o :x 7 :y rows :rotation 0}))

(defn new-tetrimino []
  (alter stopped-tetriminos conj current)
  (alter current assoc
         :piece (rand-nth (keys shapes))
         :x (rand-int (+ 2 (- cols 4)))
         :y rows))

(defn draw []
  (dosync
   (if (< (:y @current) 0)
     (new-tetrimino)
     (alter current update-in [:y] dec)))
  
  (fill 0 0 0)
  (rect 0 0 game-width-pixels game-height-pixels)
  (draw-tetrimino @current)
  )


(defn mouse-moved []
  (let [x (max 0 (min (- game-width-pixels 20) (mouse-x))) y (mouse-y)]
    (dosync (alter current assoc :x (/ x block-width)))
    ))

(defn rotate-block []
  (dosync (alter current update-in [:rotation] inc))
  )

(defn setup []
  (dosync (ref-set current {:piece :o :x 7 :y 2}))
  (smooth)
  (background 0 0 0)
  (frame-rate 3)
  )

(defsketch gen-art-1
  :title "Tetris"
  :setup setup
  :draw draw
  :mouse-moved mouse-moved
  :mouse-clicked rotate-block
  :size [game-width-pixels game-height-pixels])