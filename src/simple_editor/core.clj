(ns simple-editor.core
  (:use [simple-editor.help :only [HELP_TEXT]]
        [simple-editor.utils :only [clamp]]
        [clojure.string :only [split join]])
  (:require [lanterna.screen :as scrn]
            [simple-editor.edit :as edit])
  (:gen-class))

(defn render-state
  "Renders the current state on screen"
  [screen {:keys [lines pos window]}]
  (scrn/clear screen)
  (doall
    (map-indexed
      #(scrn/put-string screen 0 %1 %2)
      (subvec lines
              (first window)      ; Doesn't need to be clamped - cannot be < 0
              (clamp 0 (count lines) (second window)))))
  (scrn/move-cursor screen (first pos) (- (second pos) (first window)))
  (scrn/redraw screen))

(defn update-state
  "Updates the current state with the provided keypress"
  [k {:keys [lines pos] :as state}]
  (cond (= k :left)       (edit/handle-left      state)
        (= k :right)      (edit/handle-right     state)
        (= k :up)         (edit/handle-up        state)
        (= k :down)       (edit/handle-down      state)
        (= k :backspace)  (edit/handle-backspace state)
        (= k :delete)     (edit/handle-delete    state)
        (= k :home)       (edit/handle-home      state)
        (= k :end)        (edit/handle-end       state)
        :else             (edit/handle-char   k  state)))

(defn start-editing
  "Starts the editing loop. The only way to escape is by pressing escape."
  [screen {:keys [lines pos] :as state}]
  (loop [screen screen
         state  state]
    (render-state screen state)
    (let [k (scrn/get-key-blocking screen)]
      (if (= k :escape)
        (scrn/stop screen)        ; End the editing session
        (recur screen (update-state k state))))))

(defn -main
  ([] (println HELP_TEXT))
  ([& args]
   (let [filename (first args)
         lines    (split (slurp filename) #"\n")
         pos      [0 0]
         screen   (scrn/get-screen :unix)
         state    {:lines lines :pos pos}]
     (scrn/start screen)
     (start-editing               ; By using this method, we ignore any resizings and
       screen                     ; just tell the user to live with it
       (assoc state :window [0 (second (scrn/get-size screen))])))))
