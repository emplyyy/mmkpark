#!/usr/bin/env python 
# -*- coding:utf-8 -*-
import paho.mqtt.client as mqtt
import TestCv

def on_connect(client, userdata, flags, rc):
    print("Connected success")

def on_message(client, userdata, msg):
    tmp = eval(str(msg.payload).lstrip('b'))
    dict = eval(tmp)
    a = TestCv.cvcar(dict['videourl'], dict['pic'])
    global res
    res = '[' + ",".join(str(i) for i in a) + ']'
    print(res)
    del dict['videourl']
    del dict['pic']
    dict['mPycv'] = res
    client.publish('pypub', payload=str(dict), qos=0)

client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message
client.connect('localhost', 1883, 600)
client.subscribe('pysub', qos=0)
client.loop_forever()  # 保持连接
