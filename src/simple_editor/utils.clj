(ns simple-editor.utils)

(defn clamp
  "Clamps a given input such that it cannot exceed its boundaries; x in [lo, hi]"
  ([lower upper] (partial clamp lower upper))
  ([lower upper x]
   (cond (< x lower) lower
         (> x upper) upper
         :else       x)))
