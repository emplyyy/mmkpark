package com.mmkpark.mqtt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.mmkpark.entity.Device;
import com.mmkpark.entity.Msg;
import com.mmkpark.service.DeviceService;
import com.mmkpark.service.MsgService;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

/**
 * MQTT配置，消费者
 */
@Configuration
public class MqttReceiverConfig {
    /**
     * 订阅的bean名称
     */
    public static final String CHANNEL_NAME_IN = "mqttInboundChannel";

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private MsgService msgService;



    // 客户端与服务器之间的连接意外中断，服务器将发布客户端的“遗嘱”消息
    private static final byte[] WILL_DATA;

    static {
        WILL_DATA = "offline".getBytes();
    }

    @Value("${mqtt.client.username}")
    private String username;

    @Value("${mqtt.client.password}")
    private String password;

    @Value("${mqtt.client.serverURIs}")
    private String url;

    @Value("${mqtt.client.clientId}")
    private String clientId;

    @Value("${mqtt.consumer.consumerTopics}")
    private String defaultTopic;

    /**
     * MQTT连接器选项
     */
    @Bean
    public MqttConnectOptions getReceiverMqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        // 设置连接的用户名
        if (!username.trim().equals("")) {
            options.setUserName(username);
        }
        // 设置连接的密码
        options.setPassword(password.toCharArray());
        // 设置连接的地址
        options.setServerURIs(StringUtils.split(url, ","));
        // 设置超时时间 单位为秒
        options.setConnectionTimeout(10);
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送心跳判断客户端是否在线
        // 但这个方法并没有重连的机制
        options.setKeepAliveInterval(20);
        return options;
    }

    /**
     * MQTT客户端
     */
    @Bean
    public MqttPahoClientFactory receiverMqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(getReceiverMqttConnectOptions());
        return factory;
    }

    /**
     * MQTT信息通道（消费者）
     */
    @Bean(name = CHANNEL_NAME_IN)
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }


    /**
     * MQTT消息订阅绑定（消费者）
     */
    @Bean
    public MessageProducer inbound() {
        // 可以同时消费（订阅）多个Topic
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        clientId, receiverMqttClientFactory(),
                        StringUtils.split(defaultTopic, ","));
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        // 设置订阅通道
        adapter.setOutputChannel(mqttInboundChannel());
        return adapter;
    }

    /**
     * MQTT消息处理器（消费者）
     */
    @Bean
    @ServiceActivator(inputChannel = CHANNEL_NAME_IN)
    public MessageHandler handler() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
                String msg = message.getPayload().toString();

                //Json解析


                if (topic.equals("state")) {

                    JSONObject jsonObject = JSON.parseObject(msg);
                    String deviceId = String.valueOf(jsonObject.get("deviceId"));
                    if (deviceId.equals(null) && deviceId.length() == 0){ return; }
                    Device device = new Device();
                    device.setDeviceId(deviceId);
                    device.setStatus(String.valueOf(jsonObject.get("status")).trim());
                    device.setMonitorValue(String.valueOf(jsonObject.get("monitorValue")).trim());
                    deviceService.saveOrUpdate(device, new UpdateWrapper<Device>().eq("device_id", deviceId));

                } else if (topic.equals("cvcar")){


                } else if (topic.equals("piserial")){


                } else if (topic.equals("pypub")){

                    Msg pymsg = JSON.parseObject(msg, Msg.class);
                    System.out.println(msg);
                    msgService.update(pymsg, new UpdateWrapper<Msg>().eq("m_garname", pymsg.getMGarname()));

                }

            }
        };
    }
}
