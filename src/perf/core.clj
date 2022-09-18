(ns perf.core
  (:require [clojure-lsp.test-helper :as h]
            [clojure-lsp.queries :as q]
            [clojure.core.async :as async]))

(defn prepare-large-project []
  (let [components (h/components)
        still-running (atom true)]
    ;; set up consumers on channels so that they don't overflow
    (async/go-loop []
      (async/alts! [(async/timeout 5000)
                    (:current-changes-chan components)
                    (:diagnostics-chan components)
                    (:watched-files-chan components)
                    (:edits-chan components)])
      (when @still-running (recur)))
    ;; load code
    (let [[[row col]] (h/load-code-and-locs
                       (str "(ns a.b.c (:require [d.e.f :as f-alias]))\n"
                            "(defn x [filename] filename |f-alias/foo)\n"))]
      (h/load-code-and-locs "(ns d.e.f) (def foo 1)"
                            (h/file-uri "file:///b.clj"))
      (dotimes [cnt 2000]
        (h/load-code-and-locs (format "(ns d.e.u%d) (def u%d 1)" cnt cnt)
                              (h/file-uri (format "file:///u%d.clj" cnt))))
      {:row row
       :col col
       :db (h/db)
       :components components
       :teardown-fn #(reset! still-running false)})))

(defn teardown [state]
  ((:teardown-fn state))
  (h/reset-components!))

(defn find-definition [state]
  (q/find-definition-from-cursor (:db state) (h/file-path "/a.clj") (-> state :row) (-> state :col)))