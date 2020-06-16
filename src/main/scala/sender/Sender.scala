package sender

import com.typesafe.config.Config

trait Sender {
  def init(config: Config): Unit

  def open(args: Any*): Unit

  def send(obj: Any)

  def close()
}
