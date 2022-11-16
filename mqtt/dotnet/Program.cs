using MQTTnet;
using MQTTnet.Client;
using ICSharpCode.SharpZipLib.Core;
using ICSharpCode.SharpZipLib.Zip.Compression.Streams;
using System.Text;

var mqttFactory = new MqttFactory();

using (var mqttClient = mqttFactory.CreateMqttClient())
{
    var mqttClientOptions = new MqttClientOptionsBuilder().WithTcpServer("localhost").Build();
    var topicFilter = new MqttTopicFilterBuilder().WithTopic("/cle/mqtt").Build();

    mqttClient.ConnectedAsync += e =>
    {
        Console.WriteLine("Connected.");
        return Task.CompletedTask;
    };

    mqttClient.ApplicationMessageReceivedAsync += e =>
    {
        var receiveBytes = e.ApplicationMessage.Payload;
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
        catch (Exception ex)
        {
            Console.WriteLine(ex.ToString());
        }
        return Task.CompletedTask;
    };

    await mqttClient.ConnectAsync(mqttClientOptions, CancellationToken.None);
    await mqttClient.SubscribeAsync(topicFilter, CancellationToken.None);

    while (true)
        Console.ReadLine();
}