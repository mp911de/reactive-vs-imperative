Reactive vs Imperative
======================

This is a demo project containing various modules:

* `slow-server` server with a slow response (default: 5 seconds, increase to have more time to illustrate threading behavior), starts on port `9000`.
* `reactive-app` reactive application accessing `slow-server`, starts on port `9100`. Limited to 16 threads.
* `servlet-app` servlet-stack application accessing `slow-server`, starts on port `9200`. Limited to 16 threads.
* `load-generator` generates load (100 requests) to `reactive-app`/`servlet-app`.

Both applications (`reactive-app`, `servlet-app`) dump their thread count on the console.


## Load Generator on reactive app

Running the load generator should let you see something like:

```
2017-11-06 11:42:15,159  INFO          load.GenerateLoadForReactiveApp:  40 - Starting requests...
2017-11-06 11:42:15,340  INFO          load.GenerateLoadForReactiveApp:  46 - Running...
2017-11-06 11:42:21,104  INFO          load.GenerateLoadForReactiveApp:  52 - Done!
```

## Load Generator on servlet app

Running the load generator should let you see something like:

```
2017-11-06 11:42:19,674  INFO           load.GenerateLoadForServletApp:  40 - Starting requests...
2017-11-06 11:42:19,863  INFO           load.GenerateLoadForServletApp:  46 - Running...
2017-11-06 11:42:55,208  INFO           load.GenerateLoadForServletApp:  52 - Done!
```

If you pay notice to the completion time, you'll notice the difference in completion time. The end-to-end
reactive approach takes just about the duration of the wait while the imperative (Thread blocking)
takes roughly 31 seconds (100 requests / 16 threads = concurrency factor 6.25 * 5 ~ 31.25 sec).
 