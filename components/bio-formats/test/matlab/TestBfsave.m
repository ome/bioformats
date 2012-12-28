% Integration tests for the bfSave utility function
%
% Require MATLAB xUnit Test Framework to be installed
% http://www.mathworks.com/matlabcentral/fileexchange/22846-matlab-xunit-test-framework

classdef TestBfsave < TestCase
    
    properties
        reader
        path
        I = rand(50, 100, 3, 4, 5)
    end
    
    methods
        function self = TestBfsave(name)
            self = self@TestCase(name);
        end
        
        function setUp(self)
            bfCheckJavaPath();
            if isunix,
                self.path = '/tmp/test.ome.tiff';
            else
                self.path = 'C:\test.ome.tiff';
            end
        end
        
        function tearDown(self)
            self.reader.close();
            if exist(self.path,'file')==2, delete(self.path); end
        end
            
        % Dimension order tests
        function testDimensionOrderXYZCT(self)            
            self.saveAndLoad(uint8(self.I * (2^8-1)), 'XYZCT')
        end
        
        function testDimensionOrderXYZTC(self)            
            self.saveAndLoad(uint8(self.I * (2^8-1)), 'XYZTC')
        end
        
        function testDimensionOrderXYCZT(self)            
            self.saveAndLoad(uint8(self.I * (2^8-1)), 'XYCZT')
        end
        
        function testDimensionOrderXYCTZ(self)            
            self.saveAndLoad(uint8(self.I * (2^8-1)), 'XYCTZ')
        end
        
        function testDimensionOrderXYTCZ(self)            
            self.saveAndLoad(uint8(self.I * (2^8-1)), 'XYTCZ')
        end
        
        function testDimensionOrderXYTZC(self)            
            self.saveAndLoad(uint8(self.I * (2^8-1)), 'XYTZC')
        end
                
        % Data type tests
        function testPixelsTypeUINT8(self)
            self.saveAndLoad(uint8(self.I * (2^8-1)));
        end
        
        function testPixelsTypeINT8(self)
            self.saveAndLoad(int8(self.I * (2^8-1)));
        end
        
        function testPixelsTypeUINT16(self)
            self.saveAndLoad(uint16(self.I * (2^16-1)));
        end
        
        function testPixelsTypeINT16(self)
            self.saveAndLoad(uint16(self.I * (2^16-1)));
        end
        
        function testPixelsTypeUINT32(self)
            self.saveAndLoad(uint32(self.I * (2^32-1)));
        end
        
        function testPixelsTypeINT32(self)
            self.saveAndLoad(int32(self.I * (2^32-1)));
        end
        
        function testPixelsTypeFLOAT(self)
            self.saveAndLoad(single(self.I * (2^16-1)));
        end
        
        function testPixelsTypeDOUBLE(self)
            self.saveAndLoad(double(self.I * (2^16-1)));
        end
        
        %%
        function saveAndLoad(self, I, dimensionOrder)
            
            if nargin<3, dimensionOrder = 'XYZCT'; end
            
            % Create stack and save it
            bfsave(I, self.path, dimensionOrder);
            sizeZ = size(I, find(dimensionOrder=='Z'));
            sizeC = size(I, find(dimensionOrder=='C'));
            sizeT = size(I, find(dimensionOrder=='T'));
            
            % Check dimensions of saved ome-tiff
            self.reader = bfGetReader(self.path);
            assertEqual(self.reader.getSizeZ, sizeZ);
            assertEqual(self.reader.getSizeC, sizeC);
            assertEqual(self.reader.getSizeT, sizeT);
            
            % Test all planes
            for iPlane = 1 : sizeZ * sizeC * sizeT
                [i,j,k] = ind2sub([size(I, 3) size(I, 4) size(I, 5)], iPlane);
                assertEqual(I(:, :, i, j, k), bfGetPlane(self.reader, iPlane));
            end
        end
        
    end
    
end

