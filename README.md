# LaTiS server prototype for data from the CHESS project

This is a Scala project that uses the [LaTiS 3](https://github.com/latis-data/latis3) library. It requires [sbt](https://www.scala-sbt.org/) to build.

Sample datasets are defined in `datasets/fdml` with data in `datasets/data`. (Generally, data files would be managed separately.)
For more information about this project and its datasets, see https://docs.google.com/document/d/1EUzuZcbHzGs2T2v_IhRbzFYtbvglCJrXrmYhj3YkdmM/.

This server is designed to run as a Docker container. To build a docker image, run

    sbt docker

You can then run the server with

    docker run --rm -p 8080:8080 <image-id>

You can then hit the server at 

    http://localhost:8080/dap2/

In a web browser, this will present a browsable view of the dataset catalog. In most other tools, you will get a JSON representation of the catalog.

If you hit a dataset with the "meta" suffix, you will get a JSON view of the dataset metadata. 
This will indicate what variables are available.

You can get subsets of data in CSV format with server side operations using queries like:

    http://localhost:8080/dap2/uofm_mag_abk.csv?time>=2003-10-30&time<2003-10-31&formatTime("yyyy-MM-dd'T'HH:mm:ss")

Note that some tools will require you to URL encode some characters:

    http://localhost:8080/dap2/uofm_mag_abk.csv?time%3E=2003-10-30&time%3C2003-10-31&formatTime(%22yyyy-MM-dd%27T%27HH:mm:ss%22)

LaTiS 3 is under active development and the service API is not well documented. 
The most reliable source is the latis3 code itself.
There are numerous output format options as defined in the `encodeDataset` method of the [Dap2Service](https://github.com/latis-data/latis3/blob/master/dap2-service/src/main/scala/latis/service/dap2/Dap2Service.scala#L147). 
The available operations are defined in the `makeOperation` method of [UnaryOperation](https://github.com/latis-data/latis3/blob/master/core/src/main/scala/latis/ops/UnaryOperation.scala).
