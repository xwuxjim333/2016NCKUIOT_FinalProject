#!/usr/bin/python
# -*- coding: utf-8 -*-

import sys
import threading
import time
import json

sys.path.append('/home/pi/2015-iot-neat-infrastructure/M2M FunctionServer')
sys.path.append('/home/pi/2015-iot-neat-infrastructure/NIT_Module')
import NIT_Node_Module
from terminalColor import bcolors

NodeUUID = "A0108@NODE-8c987879-958f-44e4-b1ac-010801080108"
# NodeUUID ="NODE-" +uuid.uuid1()

Functions = ["key", "SW1"]
NodeFunctions = ['hotel']

print("::::::::::::::::::::::::::::::::::::::::::\n")
print("::::::::::::::::::::::::::::::::::::::::::\n")
print("'##::: ##::'#######::'########::'########:")
print(" ###:: ##:'##.... ##: ##.... ##: ##.....::")
print(" ####: ##: ##:::: ##: ##:::: ##: ##:::::::")
print(" ## ## ##: ##:::: ##: ##:::: ##: ######:::")
print(" ##. ####: ##:::: ##: ##:::: ##: ##...::::")
print(" ##:. ###: ##:::: ##: ##:::: ##: ##:::::::")
print(" ##::. ##:. #######:: ########:: ########:")
print("..::::..:::.......:::........:::........::")
print("::::::::::::::::::::::::::::::::::::::::::\n")

nit = NIT_Node_Module.NIT_Node(NodeUUID, Functions, NodeFunctions)


# Connect to MQTT Server for communication
def NodeToServerMQTTThread():
    # print("thread name：　" + threading.current_thread().getName())

    # callback
    nit.CallBackRxRouting = RxRouting
    print(bcolors.HEADER + '===============================================\n' + bcolors.ENDC)
    print(bcolors.HEADER + '---------------Node(%s)--->>>Server in MQTT-\n' % NodeUUID + bcolors.ENDC)
    print(bcolors.HEADER + '>>>Start connect Server %s<<<' % (
        time.asctime(time.localtime(time.time()))) + bcolors.ENDC)
    print(bcolors.HEADER + '===============================================\n' + bcolors.ENDC)
    print(bcolors.HEADER + 'Register to IoT Server successful! \n' + bcolors.ENDC)

    try:

        nit.RegisterNoode();

    except (RuntimeError, TypeError, NameError) as e:
        print(bcolors.FAIL + "[INFO]Register error." + str(e) + bcolors.ENDC)
        raise
        sys.exit(1)


########### Keyboard interactive ##############
def RxRouting(self, _obj_json_msg):
    nit.M2M_RxRouting(_obj_json_msg)


global flip
def loop():
    global flip
    decide = "g"
    decide = input("enter 't' to trigger")
    print(decide)

    initMSGObj = {'TopicName': "A0108@NODE-8c987879-958f-44e4-b1ac-010801080108/SW1", 'Control': 'M2M_SET', 'Source': "A0108@NODE-8c987879-958f-44e4-b1ac-010801080108", 'M2M_Value': flip}
    initMSGSTR = json.dumps(initMSGObj)

    if (decide == "t"):
        nit.DirectMSG("A0108@NODE-8c987879-958f-44e4-b1ac-010801080108/SW1", initMSGSTR)
        print("SW01 SENT.")
        flip = (~flip)


if __name__ == "__main__":
    MQTT_Thread = threading.Thread(target=NodeToServerMQTTThread, name="main_thread")
    MQTT_Thread.start()
    global flip
    flip = 0
    while True:
        loop()
