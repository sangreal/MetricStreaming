package sender
import com.typesafe.config.Config

class TSDBSender extends Sender {
  override def init(config: Config): Unit = {
    println("init")
  }

  override def open(args: Any*): Unit = {

  }

  override def send(obj: Any): Unit = {

  }

  override def close(): Unit = {

  }
}
