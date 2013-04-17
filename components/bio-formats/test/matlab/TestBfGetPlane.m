% Tests for the bfGetPlane utility function
%
% Require MATLAB xUnit Test Framework to be installed
% http://www.mathworks.com/matlabcentral/fileexchange/22846-matlab-xunit-test-framework

% OME Bio-Formats package for reading and converting biological file formats.
%
% Copyright (C) 2012 - 2013 Open Microscopy Environment:
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

classdef TestBfGetPlane < TestCase
    
    properties
        reader
    end
    
    methods
        function self = TestBfGetPlane(name)
            self = self@TestCase(name);
        end
        
        function setUp(self)
            bfCheckJavaPath();
            self.reader = loci.formats.ChannelSeparator(loci.formats.in.FakeReader());
        end
        
        function tearDown(self)
            self.reader.close();
            self.reader = [];
        end
        
        % Pixel type tests
        function checkPixelsType(self, pixelsType)
            self.reader.setId([pixelsType '-test&pixelType=' pixelsType '.fake']);
            I = bfGetPlane(self.reader, 1);
            if strcmp(pixelsType, 'float')
                assertEqual(class(I), 'single');
            else
                assertEqual(class(I), pixelsType);
            end
        end
        
        function testINT8(self)
            self.checkPixelsType('int8');
        end
        function testUINT8(self)
            self.checkPixelsType('uint8');
        end
        function testINT16(self)
            self.checkPixelsType('int16');
        end
        function testUINT16(self)
            self.checkPixelsType('uint16');
        end
        function testINT32(self)
            self.checkPixelsType('int32');
        end
        function testUINT32(self)
            self.checkPixelsType('uint32');
        end
        function testSINGLE(self)
            self.checkPixelsType('float');
        end
        function testDOUBLE(self)
            self.checkPixelsType('double');
        end
        
        % Dimension size tests
        function testSizeX(self)
            sizeX = 200;
            self.reader = bfGetReader(['sizeX-test&sizeX=' num2str(sizeX) '.fake']);
            I = bfGetPlane(self.reader, 1);
            assertEqual(size(I, 2), sizeX);
        end
        
        function testSizeY(self)
            sizeY = 200;
            self.reader = bfGetReader(['sizeY-test&sizeY=' num2str(sizeY) '.fake']);
            I = bfGetPlane(self.reader, 1);
            assertEqual(size(I, 1), sizeY);
        end
        
        % Tile tests
        function checkTile(self, x, y, w, h)
            I = bfGetPlane(self.reader, 1);
            I2 = bfGetPlane(self.reader, 1, x, y, w, h);
            
            assertEqual(I2, I(y : y + h - 1, x : x + w - 1));
        end
        
        function testFullTile(self)
            self.reader.setId('fulltile-test.fake')
            self.checkTile(1, 1, self.reader.getSizeX(), self.reader.getSizeY())
        end
        
        function testSquareTile(self)
            self.reader.setId('sqtile-test.fake')
            self.checkTile(10, 10, 20, 20)
        end
        
        function testRectangularTile(self)
            self.reader.setId('recttile-test.fake')
            self.checkTile(20, 10, 40, 20);
        end
        
        function testSingleTile(self)
            self.reader.setId('fulltile-test.fake')
            self.checkTile(50, 50, 1, 1);
        end
    end
end
