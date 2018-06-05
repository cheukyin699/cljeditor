(ns simple-editor.edit)

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
  state)

(defn handle-down
  "Goes down until we hit the lower-most boundary, and pushes the window down until we get to the end of the file"
  [{:keys [lines pos window] :as state}]
  state)

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
  state)

(defn handle-delete
  "Removes the character on the cursor; if none exist, removes the line below"
  [{:keys [lines pos window] :as state}]
  state)

(defn handle-char
  "Inserts a character on the cursor, advances the cursor to the right by one; if character is an enter, inserts a line"
  [k {:keys [lines pos window] :as state}]
  state)
