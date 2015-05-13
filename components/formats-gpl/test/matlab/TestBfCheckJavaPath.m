% TestBfCheckJavaPath define test cases for bfCheckJavaPath utility function
%
% Require MATLAB xUnit Test Framework to be installed
% http://www.mathworks.com/matlabcentral/fileexchange/22846-matlab-xunit-test-framework
% https://github.com/psexton/matlab-xunit (GitHub source code)

% OME Bio-Formats package for reading and converting biological file formats.
%
% Copyright (C) 2012 - 2015 Open Microscopy Environment:
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


classdef TestBfCheckJavaPath < TestBfMatlab
    
    properties
        status
        version
        maxTime = .1
    end
    
    methods
        function self = TestBfCheckJavaPath(name)
            self = self@TestBfMatlab(name);
        end
        
        function testDefault(self)
            self.status = bfCheckJavaPath();
            assertTrue(self.status);
        end
        
        function testAutoloadBioformats(self)
            self.status = bfCheckJavaPath(true);
            assertTrue(self.status);
        end
        
        function testNoAutoloadBioformats(self)
            isStatic = ismember(self.jarPath,...
                javaclasspath('-static'));
            self.status = bfCheckJavaPath(false);
            if isStatic
                assertTrue(self.status);
            else
                assertFalse(self.status);
            end
        end
        
        function testPerformance(self)
            nCounts = 10;
            times = zeros(nCounts);
            for i = 1 : nCounts
                tic;
                bfCheckJavaPath();
                times(i) = toc;
            end
            
            % First call should be the longest as the Bio-Formats JAR file is
            % added to the javaclasspath
            assertTrue(times(1) > times(2));
            % Second call should still be longer than all the following
            % ones. Profiling reveals some amount of time is spent while
            % computing javaclasspath.local_get_static_path
            assertTrue(all(times(2) > times(3:end)));
            % From the third call and onwards, javaclasspath and thus
            % bfCheckJavaPath should return fast
            assertTrue(mean(times(3:end)) < self.maxTime);
        end
        
        function testJavaMethod(self)
            self.status = bfCheckJavaPath(true);
            version = char(loci.formats.FormatTools.VERSION);
            [self.status self.version]= bfCheckJavaPath(false);
            assertEqual(self.version,version);
            if (exist ('OCTAVE_VERSION', 'builtin'))
                version = char(java_get('loci.formats.FormatTools', 'VERSION'));
                assertEqual( self.version, version);
            end
        end
    end
end
