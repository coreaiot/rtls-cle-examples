import { connect } from 'mqtt';
import { unzip } from 'zlib';

const topic = '/cle/mqtt';
const c = connect('mqtt://localhost');

console.log('Connecting ...');

c.on('connect', () => {
  console.log('Connected.')
  c.subscribe(topic);
});

c.on('message', (t, msg) => {
  if (t === topic) {
    unzip(msg, (err, buffer) => {
      if (err) return;
      const json = buffer.toString();
      console.log(json);
    });
  }
});