Bio-Formats plugin configuration options
========================================

The Bio-Formats plugin can be configured by opening the **Bio-Formats Plugins 
Configuration** dialog from the Plugin menu. The General tab allows for 
configuration of features for the plugin such as upgrade checking or modifying 
the slice label pattern.

Configuring the slice label pattern
-----------------------------------

The slice label is the text displayed at the top of the image window in ImageJ.
The values displayed here can be modified by configuring the slice label pattern.

The list of available parameters for configuration is as follows::
  :%s: series index
  :%n: series name
  :%c: channel index
  :%w: channel name
  :%z: Z index
  :%t: T index
  :%A: acquisition timestamp

Each index value will be 1-based rather than 0-based. The index will be displayed 
along with the total dimension count and with a prefix for the particular dimension. 
For example using ``%c`` for channel index will result in the display ``c:1/3``.
