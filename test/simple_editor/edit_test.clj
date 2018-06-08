(ns simple-editor.edit-test
  (:require [clojure.test :refer :all]
            [simple-editor.edit :refer :all]))

(def lengths [      6           9      4            10])
(def content ["abcdef" "hijklmnop" "tsra" "qrstuvwxyz"])

(defn create-state
  "Creates a state where you provide the positions (and windows)"
  ([pos] {:lines content :pos pos :window [0 200]})
  ([content pos] {:lines content :pos pos :window [0 200]}))

(deftest unittest-left
  (testing "Basic cases"
    (testing "moving from middle"
      (is (= (handle-left (create-state [1 1]))
             (create-state [0 1]))))
    (testing "moving from right-most edge"
      (is (= (handle-left (create-state [9 1]))
             (create-state [8 1]))))
    (testing "moving from left-most edge"
      (is (= (handle-left (create-state [0 1]))
             (create-state [0 1]))))))

(deftest unittest-right
  (testing "Basic cases"
    (testing "moving from middle"
      (is (= (handle-right (create-state [1 1]))
             (create-state [2 1]))))
    (testing "moving from right-most edge"
      (is (= (handle-right (create-state [9 1]))
             (create-state [9 1]))))
    (testing "moving from left-most edge"
      (is (= (handle-right (create-state [0 1]))
             (create-state [1 1]))))))

(deftest unittest-up
  (testing "Top cases"
    (is (= (handle-up (create-state [6 0]))
           (create-state [6 0]))))
  (testing "Middle cases"
    (testing "moving from middle to middle"
      (is (= (handle-up (create-state [1 1]))
             (create-state [1 0]))))
    (testing "moving from middle to shorter"
      (is (= (handle-up (create-state [8 1]))
             (create-state [6 0]))))
    (testing "moving from middle to longer"
      (is (= (handle-up (create-state [4 2]))
             (create-state [4 1])))))
  (testing "Low cases"
    (is (= (handle-up (create-state [10 3]))
           (create-state [4 2])))))

(deftest unittest-down
  (testing "Top cases"
    (is (= (handle-down (create-state [6 0]))
           (create-state [6 1]))))
  (testing "Middle cases"
    (testing "moving from middle to middle"
      (is (= (handle-down (create-state [1 1]))
             (create-state [1 2]))))
    (testing "moving from middle to shorter"
      (is (= (handle-down (create-state [8 1]))
             (create-state [4 2]))))
    (testing "moving from middle to longer"
      (is (= (handle-down (create-state [4 2]))
             (create-state [4 3])))))
  (testing "Low cases"
    (is (= (handle-down (create-state [10 3]))
           (create-state [10 3])))))

(deftest unittest-backspace
  (testing "Basic cases"
    (testing "backspace from middle"
      (is (= (handle-backspace (create-state [1 1]))
             (create-state ["abcdef" "ijklmnop" "tsra" "qrstuvwxyz"] [0 1]))))
    (testing "backspace from start of line"
      (is (= (handle-backspace (create-state [0 1]))
             (create-state ["abcdefhijklmnop" "tsra" "qrstuvwxyz"] [6 0])))))
  (testing "Edge cases"
    (testing "backspace from beginning of file"
      (is (= (handle-backspace (create-state [0 0]))
             (create-state [0 0]))))))

(deftest unittest-delete
  (testing "Basic cases"
    (testing "delete from middle"
      (is (= (handle-delete (create-state [1 1]))
             (create-state ["abcdef" "hjklmnop" "tsra" "qrstuvwxyz"] [1 1]))))
    (testing "delete from end of line"
      (is (= (handle-delete (create-state [9 1]))
             (create-state ["abcdef" "hijklmnoptsra" "qrstuvwxyz"] [9 1])))))
  (testing "Edge cases"
    (testing "delete from end of file"
      (is (= (handle-delete (create-state [10 3]))
             (create-state [10 3]))))))

(deftest unittest-home
  (testing "Basic cases"
    (testing "moving from middle"
      (is (= (handle-home (create-state [1 1]))
             (create-state [0 1]))))
    (testing "moving from start of line"
      (is (= (handle-home (create-state [0 1]))
             (create-state [0 1]))))
    (testing "moving from end of line"
      (is (= (handle-home (create-state [9 1]))
             (create-state [0 1]))))))

(deftest unittest-end
  (testing "Basic cases"
    (testing "moving from middle"
      (is (= (handle-end (create-state [1 1]))
             (create-state [9 1]))))
    (testing "moving from start of line"
      (is (= (handle-end (create-state [0 1]))
             (create-state [9 1]))))
    (testing "moving from end of line"
      (is (= (handle-end (create-state [9 1]))
             (create-state [9 1]))))))

(deftest unittest-chars
  (testing "Basic cases"
    (testing "some characters"
      (is (= (handle-char \a (create-state [1 1]))
             (create-state ["abcdef" "haijklmnop" "tsra" "qrstuvwxyz"] [2 1]))))
    (testing "new line at end of line"
      (is (= (handle-char :enter (create-state [9 1]))
             (create-state ["abcdef" "hijklmnop" "" "tsra" "qrstuvwxyz"] [0 2]))))
    (testing "new line in middle of line"
      (is (= (handle-char :enter (create-state [1 1]))
             (create-state ["abcdef" "h" "ijklmnop" "tsra" "qrstuvwxyz"] [0 2]))))
    (testing "new line at start of line"
      (is (= (handle-char :enter (create-state [0 1]))
             (create-state ["abcdef" "" "hijklmnop" "tsra" "qrstuvwxyz"] [0 2]))))))

