var config = {};

config.debug = process.env.DEBUG || true;

config.mqtt  = {};
config.mqtt.namespace = process.env.MQTT_NAMESPACE || '#';
config.mqtt.hostname  = process.env.MQTT_HOSTNAME  || '172.31.21.171';
config.mqtt.port      = process.env.MQTT_PORT      || 1883;


module.exports = config;