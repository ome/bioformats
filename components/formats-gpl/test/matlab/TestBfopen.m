% Integration tests for the bfopen utility function
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

classdef TestBfopen < ReaderTest
    
    properties
        filepath
        data
        nSeries = 1
        flag = 1
        width = 10;
        height = 10;
        x = 10;
        y = 10;
        bpp
    end
    
    methods
        function self = TestBfopen(name)
            self = self@ReaderTest(name);
        end
        
        function tearDown(self)
            if exist(self.tmpdir, 'dir') == 7, rmdir(self.tmpdir, 's'); end
            self.data = [];
            tearDown@ReaderTest(self);
        end
        
        function checkFake(self, fakefilename)
            % Create fake file
            mkdir(self.tmpdir);
            self.filepath = fullfile(self.tmpdir, fakefilename);
            fid = fopen(self.filepath, 'w+');
            fclose(fid);
            
            for flag = 1:2
                self.flag = flag;
                % Read fake file using bfopen
                if (self.flag == 1)
                    self.data = bfopen(self.filepath);
                    self.x = self.sizeY;
                    self.y = self.sizeX;
                elseif (self.flag == 2)
                    self.data = bfopen(self.filepath,self.x,self.y,self.width,self.height);
                end
                
                % Test dimensions of bfopen output and core metadata
                nPlanes = self.sizeZ * self.sizeC * self.sizeT;
                assertEqual(size(self.data), [self.nSeries 4]);
                for i = 1 : self.nSeries
                    assertEqual(size(self.data{i, 1}), [nPlanes 2]);
                    m = self.data{i,4};
                    TileSize = size(self.data{1,1}{1,1});
                    assertEqual(m.getImageCount(), self.nSeries);
                    assertEqual(m.getPixelsSizeZ(i-1).getValue(), self.sizeZ);
                    assertEqual(m.getPixelsSizeC(i-1).getValue(), self.sizeC);
                    assertEqual(m.getPixelsSizeT(i-1).getValue(), self.sizeT);
                    assertEqual(m.getPixelsSizeX(i-1).getValue(), self.sizeX);
                    assertEqual(m.getPixelsSizeY(i-1).getValue(), self.sizeY);
                    assertEqual(TileSize(1), self.x);
                    assertEqual(TileSize(2), self.y);
                    
                end
                
                self.x = 10;
                self.y = 10;
            end
        end
        
        % Dimension tests
        function testDefault(self)
            self.checkFake('test.fake')
        end
        
        function testMultiSeries(self)
            self.nSeries = 3;
            self.checkFake(['test&series=' num2str(self.nSeries) '.fake'])
        end
        
        function testSizeZ(self)
            self.sizeZ = 3;
            self.checkFake(['test&sizeZ=' num2str(self.sizeZ) '.fake'])
        end
        
        function testSizeC(self)
            self.sizeC = 3;
            self.checkFake(['test&sizeC=' num2str(self.sizeC) '.fake'])
        end
        
        function testSizeT(self)
            self.sizeT = 3;
            self.checkFake(['test&sizeT=' num2str(self.sizeT) '.fake'])
        end
        
        
        function testJavaMethod(self)
            logLevel = loci.common.DebugTools.enableLogging('INFO');
            logLevel1 = javaMethod('enableLogging', 'loci.common.DebugTools', 'INFO');
            
            assertEqual(logLevel,logLevel1);
            
            self.reader = bfGetReader('test.fake', 0);
            pixelType = self.reader.getPixelType();
            self.bpp = loci.formats.FormatTools.getBytesPerPixel(pixelType);
            bpp = javaMethod('getBytesPerPixel', 'loci.formats.FormatTools', ...
                pixelType);
            
            assertEqual(self.bpp,bpp);
            
        end
        
        % Colormap tests
        function testNoColormap(self)
            self.sizeC = 3;
            self.checkFake('test&indexed=true&falseColor=false.fake');
            assertTrue(isempty(self.data{3}{1}));
        end
        
        function test8BitColormap(self)
            self.checkFake('test&indexed=true&falseColor=true&pixelType=uint8.fake');
            assertTrue(isa(self.data{3}{1}, 'single'));
        end
        
        function test16BitColormap(self)
            self.checkFake('test&indexed=true&falseColor=true&pixelType=uint16.fake');
            assertTrue(isa(self.data{3}{1}, 'single'));
        end
        
    end
end
