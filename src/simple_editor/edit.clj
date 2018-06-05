(ns simple-editor.edit)

(defn handle-left
  [{:keys [lines pos window] :as state}]
  state)

(defn handle-right
  [{:keys [lines pos window] :as state}]
  state)

(defn handle-up
  [{:keys [lines pos window] :as state}]
  state)

(defn handle-down
  [{:keys [lines pos window] :as state}]
  state)

(defn handle-backspace
  [{:keys [lines pos window] :as state}]
  state)

(defn handle-delete
  [{:keys [lines pos window] :as state}]
  state)

(defn handle-char
  [k {:keys [lines pos window] :as state}]
  state)
