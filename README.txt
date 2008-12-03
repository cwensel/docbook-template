
For every docbook target, create a sub directory under
src/docbook <- docbook file
src/images <- all svg images
src/examples <- all unit tests, if any

For example, for a HBase article

src/docbook/hbase/hbase.xml
src/images/hbase/
src/examples/hbase/

To compile the docbook based manuals...

 ant -Dtarget.doc=hbase all
 
To edit with an external editor

 ant -Dtarget.doc=hbase edit
 
This creates and 'edit' directory with all rendered images and extracted source code for inclusion.

Re-run "edit" to copy your edits back to the correct directory so the changes can be checked in. 
The ant file looks to see which xml is newer. Only edit the docbook xml, includes must be edited from the
src tree.



