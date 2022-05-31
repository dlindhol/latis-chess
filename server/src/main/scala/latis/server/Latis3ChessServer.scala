package latis.server

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import cats.effect.Resource
import fs2.io.file.Path
import org.typelevel.log4cats.slf4j.Slf4jLogger

import latis.catalog.FdmlCatalog
import latis.server.Latis3ServerBuilder._
import latis.service.dap2.Dap2Service

object Latis3ChessServer extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    (for {
      logger      <- Resource.eval(Slf4jLogger.create[IO])
      serverConf  <- Resource.eval(getServerConf)
      catalogConf <- Resource.eval(getCatalogConf)
      catalog     <- Resource.eval(
        FdmlCatalog.fromDirectory(Path.fromNioPath(catalogConf.dir), catalogConf.validate)
      )
      interfaces   = List(
        "dap2" -> new Dap2Service(catalog)
      )
      server      <- mkServer(serverConf, interfaces, logger)
    } yield server)
      .use(_ => IO.never)
      .as(ExitCode.Success)
}
