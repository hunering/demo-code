import MySQLdb as mdb
import sys

try:
    con = mdb.connect('localhost2', 'root', '', 'user');

    cur = con.cursor()
    #cur.execute("SELECT VERSION()")    
    #ver = cur.fetchone()
    #print "Database version : %s " % ver
    
    cur.execute("show databases")
    rows = cur.fetchall()
    for row in rows:
        print " %s " % row
    
except mdb.Error, e:
  
    print "Error %d: %s" % (e.args[0],e.args[1])
    sys.exit(1)
    
finally:    
        
    if con:    
        con.close()
        
def listSchemas(con):
    cur = con.cursor()
    cur.execute("show databases")
    rows = cur.fetchall()
    for row in rows:
        print " %s " % row
        
def funcsome():
    try:
        con = mdb.connect('100.99.30.86', 'lfdev', 'user_2015', 'basic');
    
        cur = con.cursor()
        #cur.execute("SELECT VERSION()")    
        #ver = cur.fetchone()
        #print "Database version : %s " % ver
        
        cur.execute("show databases")
        rows = cur.fetchall()
        for row in rows:
            yield " %s " % row
        
    except mdb.Error, e:  
            yield "Error %d: %s" % (e.args[0],e.args[1])
            
    
    finally:            
        if con:    
            con.close()
            
    try:
        con = mdb.connect('100.99.30.861', 'lfdev', 'user_2015', 'basic');
    
        cur = con.cursor()
        #cur.execute("SELECT VERSION()")    
        #ver = cur.fetchone()
        #print "Database version : %s " % ver
        
        cur.execute("show databases")
        rows = cur.fetchall()
        for row in rows:
            yield " %s " % row
        
    except mdb.Error, e:  
        yield "Error %d: %s" % (e.args[0],e.args[1])
            
    
    finally:            
        if con:    
            con.close()

