% Tests for the bfCheckJavaMemory utility function
%
% Require MATLAB xUnit Test Framework to be installed
% http://www.mathworks.com/matlabcentral/fileexchange/22846-matlab-xunit-test-framework
% https://github.com/psexton/matlab-xunit (GitHub source code)

% OME Bio-Formats package for reading and converting biological file formats.
%
% Copyright (C) 2014 - 2015 Open Microscopy Environment:
%   - Board of Regents of the University of Wisconsin-Madison
%   - Glencoe Software, Inc.
%   - University of Dundee
%
% This program is free software: you can redistribute it and/or modify
% it under the terms of the GNU General Public License as
% published by the Free Software Foundation, either version 2 of the
% License, or (at your option) any later version.
%
% This program is distributed in the hope that it will be useful,
% but WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
% GNU General Public License for more details.
%
% You should have received a copy of the GNU General Public License along
% with this program; if not, write to the Free Software Foundation, Inc.,
% 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

classdef TestBfCheckJavaMemory < TestBfMatlab
    
    properties
        minMemory
        warning_id = ''
    end
    
    methods
        function self = TestBfCheckJavaMemory(name)
            self = self@TestBfMatlab(name);
        end
        
        % Dimension size tests
        function runJavaMemoryCheck(self)
            lastwarn('');
            bfCheckJavaMemory(self.minMemory)
            [last_warning_msg, last_warning_id] = lastwarn;
            assertEqual(last_warning_id, self.warning_id);
            lastwarn('');
        end
        
        function testZero(self)
            self.minMemory = 0;
            self.runJavaMemoryCheck()
        end
        
        function testMaxMemory(self)
            self.minMemory = self.getRuntime();
            self.runJavaMemoryCheck()
        end
        
        function testLowMemory(self)
            self.minMemory = round(self.getRuntime() + 100);
            self.warning_id = 'BF:lowJavaMemory';
            self.runJavaMemoryCheck()
        end
    end
    methods(Static)
        
        function memory = getRuntime()
            runtime = java.lang.Runtime.getRuntime();
            memory = runtime.maxMemory() / (1024 * 1024);
        end
    end
    
end
