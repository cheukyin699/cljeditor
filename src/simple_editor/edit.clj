(ns simple-editor.edit
  (:use [simple-editor.utils :only [clamp]]
        [clojure.string :only [join]]))

(defn handle-left
  "Goes left until we hit the left-most boundary, and stop there"
  [{:keys [lines pos window] :as state}]
  (if (= 0 (first pos))
    state
    (assoc state :pos [(dec (first pos)) (second pos)])))

(defn handle-right
  "Goes right until we hit 1 more than the right-most character, and stop there"
  [{:keys [lines pos window] :as state}]
  (let [px (first pos)
        py (second pos)
        curr-line (nth lines py)]
    (if (= px (count curr-line))
      state
      (assoc state :pos [(inc px) py]))))

(defn handle-up
  "Goes up until we hit the upper-most boundary, and pushes the window up until we get to the beginning of the file"
  [{:keys [lines pos window] :as state}]
  (let [win-top (first window)
        win-bot (second window)
        pos-x   (first pos)
        pos-y   (second pos)
        next-y  (if (= 0 pos-y) pos-y (dec pos-y))      ; The next y-position, unless 0
        lamp    (clamp 0 (count (nth lines next-y)))]
    (cond (= 0 pos-y)       state
          (= win-top pos-y) (assoc state :window [(dec win-top) (dec win-bot)]
                                         :pos    [(lamp pos-x)  (dec pos-y)])
          :else             (assoc state :pos    [(lamp pos-x)  (dec pos-y)]))))

(defn handle-down
  "Goes down until we hit the lower-most boundary, and pushes the window down until we get to the end of the file"
  [{:keys [lines pos window] :as state}]
  (let [win-top (first window)
        win-bot (second window)
        pos-x   (first pos)
        pos-y   (second pos)
        c-lines (dec (count lines))         ; Line count
        next-y  (if (= c-lines pos-y) pos-y (inc pos-y))
        lamp    (clamp 0 (count (nth lines next-y)))]
    (cond (= c-lines pos-y) state
          (= win-bot pos-y) (assoc state :window [(inc win-top) (inc win-bot)]
                                         :pos    [(lamp pos-x)  (inc pos-y)])
          :else             (assoc state :pos    [(lamp pos-x)  (inc pos-y)]))))

(defn handle-home
  "Cursor goes to the start of the line"
  [{:keys [lines pos window] :as state}]
  (assoc state :pos [0 (second pos)]))

(defn handle-end
  "Cursor goes to the end of the line"
  [{:keys [lines pos window] :as state}]
  (let [curr-line (nth lines (second pos))]
    (assoc state :pos [(count curr-line) (second pos)])))

(defn handle-backspace
  "Removes the character behind the cursor; if none exist, removes the line"
  [{:keys [lines pos window] :as state}]
  (let [win-top (first window)
        win-bot (second window)
        pos-x   (first pos)
        pos-y   (second pos)
        current (nth lines pos-y)
        c-lines (dec (count lines))]
    (cond (= 0 pos-x pos-y)   ; Does nothing when it isn't necessary
          state
          (= 0 pos-x)         ; Deletes a new line
          (assoc state :pos     [(count (nth lines (dec pos-y)))
                                 (dec pos-y)]
                       :lines   (vec (concat (subvec lines 0 (dec pos-y))
                                             (vector (str (nth lines (dec pos-y))
                                                          current))
                                             (subvec lines (inc pos-y))))
                       :window  (if (= pos-y win-top)
                                  [(dec win-top) (dec win-bot)]
                                  window))
          :else               ; Everything other than new line
          (assoc state :pos   [(dec pos-x) pos-y]
                       :lines (assoc lines pos-y (str (subs current 0 (dec pos-x))
                                                      (subs current pos-x)))))))

(defn handle-delete
  "Removes the character on the cursor; if none exist, removes the line below"
  [{:keys [lines pos window] :as state}]
  (let [win-top (first window)
        win-bot (second window)
        pos-x   (first pos)
        pos-y   (second pos)
        current (nth lines pos-y)
        c-lines (dec (count lines))]
    (cond (and (= c-lines pos-y)            ; Does nothing when it isn't necessary
               (= (count current) pos-x))
          state
          (= (count current) pos-x)         ; Deletes a new line
          (assoc state :lines (vec (concat (subvec lines 0 pos-y)
                                           (vector (str current
                                                        (nth lines (inc pos-y))))
                                           (subvec lines (clamp 0 c-lines (+ pos-y 2))))))
          :else                             ; Everything other than new line
          (assoc state :lines (assoc lines pos-y (str (subs current 0 pos-x)
                                                      (subs current (inc pos-x))))))))

(defn handle-char
  "Inserts a character on the cursor, advances the cursor to the right by one; if character is an enter, inserts a line"
  [k {:keys [lines pos window] :as state}]
  (let [win-top (first window)
        win-bot (second window)
        pos-x   (first pos)
        pos-y   (second pos)
        current (nth lines pos-y)
        c-lines (dec (count lines))]
    (if (= k :enter)
      (assoc state :lines  (vec (concat (subvec lines 0 pos-y)
                                        (vector (subs current 0 pos-x)
                                                (subs current pos-x))
                                        (subvec lines (inc pos-y))))
                   :pos    [0 (inc pos-y)]
                   :window (if (= pos-y win-bot)
                             [(inc win-top) (inc win-bot)]
                             window))
      (assoc state :lines  (vec (concat (subvec lines 0 pos-y)
                                        (vector (str (subs current 0 pos-x)
                                                     k
                                                     (subs current pos-x)))
                                        (subvec lines (inc pos-y))))
                   :pos    [(inc pos-x) pos-y]))))

(defn handle-save
  "Saves the file (side effect)"
  [filename {:keys [lines] :as state}]
  (println (format "Save to '%s'? (Y/N)" filename))
  (if (= (read-line) "Y")
    (spit filename (join "\n" lines))
    (println "Not saved")))
