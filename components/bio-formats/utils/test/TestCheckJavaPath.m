classdef TestCheckJavaPath < TestCase
    
    properties
        isStatic
    end
    
    methods
        function self = TestCheckJavaPath(name)
            self = self@TestCase(name);
        end
        function setUp(self)
            % Get path to loci_tools
            lociToolsPath = which('loci_tools.jar');
            assert(~isempty(lociToolsPath));
            
            % Remove loci_tools from dynamic class path
            if ismember(lociToolsPath,javaclasspath('-dynamic'))
                javarmpath(lociToolsPath);
            end
            
            % Check if loci_tools is in the static class path
            self.isStatic = ismember(lociToolsPath,...
                javaclasspath('-static'));
        end
        
        function testDefault(self)
            status = bfCheckJavaPath();
            assertTrue(status);
        end
        
        function testAutoloadBioformats(self)
            status = bfCheckJavaPath(true);
            assertTrue(status);
        end
        
        function testNoAutoloadBioformats(self)
            status = bfCheckJavaPath(false);
            if self.isStatic
                assertTrue(status);
            else
                assertFalse(status);
            end
            
        end
        
         function testPerformance(self)
             tic;
             bfCheckJavaPath();
             totalCheckTime=toc;
             assert(totalCheckTime<1);           
        end
    end    
end