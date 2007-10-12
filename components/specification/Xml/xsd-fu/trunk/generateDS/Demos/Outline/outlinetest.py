
import os
import outline_extended
import unittest


class TestParse(unittest.TestCase):
    def testParse(self):
        """parse should return a non-None value"""
        rootObj = outline_extended.parse('outline.xml')
        self.assertNotEqual(rootObj, None)
    def testcompareParse(self):
        """export from parse should equal outline.out"""
        rootObj = outline_extended.parse('outline.xml')
        outfile = open('tmp.out', 'w')
        outfile.write('<?xml version="1.0" ?>\n')
        rootObj.export(outfile, 0)
        outfile.close()
        outfile = os.popen('diff outline.xml tmp.out', 'r')
        diffResult = outfile.read()
        outfile.close()
        self.assertEqual(diffResult, '')
    def testUnitTest(self):
        """a test of unittest"""
        self.assertEqual(1, 1)


if __name__ == "__main__":
    unittest.main()


