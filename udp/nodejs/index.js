import { createSocket } from 'dgram';
import { unzip } from 'zlib';

const client = createSocket('udp4');
client.send('subscribe:zlib', 55555, '127.0.0.1');
client.on('message', msg => {
  try {
    unzip(msg, (err, buffer) => {
      if(err) return;
      const json = buffer.toString();
      console.log(json);
    });
  } catch (e) {
    console.error(e);
  }
});