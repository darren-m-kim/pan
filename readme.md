# Server Side
## to run
$ lein run
## to test
$ lein test
# Client Side
## Installation
* clojure cli
* nodejs, npm, yarn
## starting template
https://www.youtube.com/watch?v=p61lhOvQg2Q&t=337s
## Run on terminal
$ yarn install
$ yarn start
## Connect REPL
m-x cider-connect
select 'localhost'
enter port 9000
shadow.user> (shadow/repl :app)
# Naming Convention
* Use :as but not :refer.
* Abbreviate evey imported ns into one letter alphabet.
