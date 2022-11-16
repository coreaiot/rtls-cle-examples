﻿using System.Net;
using System.Net.Sockets;
using System.Text;
using ICSharpCode.SharpZipLib.Core;
using ICSharpCode.SharpZipLib.Zip.Compression.Streams;

var hostname = "127.0.0.1";
var port = 55555;

var udpClient = new UdpClient();
try
{
    udpClient.Connect(hostname, port);
    var sendBytes = Encoding.UTF8.GetBytes("subscribe:zlib");
    udpClient.Send(sendBytes, sendBytes.Length);

    var RemoteIpEndPoint = new IPEndPoint(IPAddress.Parse(hostname), port);
    while (true)
    {
        var receiveBytes = udpClient.Receive(ref RemoteIpEndPoint);
        var output = new MemoryStream();
        try
        {
            var dataBuffer = new byte[4096];
            using (var compressedStream = new MemoryStream(receiveBytes))
            using (var stream = new InflaterInputStream(compressedStream))
                StreamUtils.Copy(stream, output, dataBuffer);
            var json = Encoding.UTF8.GetString(output.ToArray());
            Console.WriteLine(json);
        }
        catch (Exception e)
        {
            Console.WriteLine(e.ToString());
        }
    }
}
catch (Exception e)
{
    Console.WriteLine(e.ToString());
}