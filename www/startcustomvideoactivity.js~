var StartCustomVideoActivity = function() {}

StartCustomVideoActivity.prototype.video = function(orientation, success, fail) {
	return cordova.exec(success, fail, "StartCustomVideoActivity", "video", [orientation])
}

if ( ! window.plugins ) {
  window.plugins = {}
}
if ( ! window.plugins.orientationLock ) {
  window.plugins.startcustomvideoactivity = new StartCustomVideoActivity()
}

module.exports = StartCustomVideoActivity
