
# coding: utf-8

# In[21]:


from IPython.display import HTML
#HTML for toggle
HTML('''<script>
code_show=true; 
function code_toggle() {
 if (code_show){
 $('div.input').hide();
 } else {
 $('div.input').show();
 }
 code_show = !code_show
} 
$( document ).ready(code_toggle);
</script>
<form action="javascript:code_toggle()"><input type="submit" value="Click here to toggle on/off the raw code."></form>''')



# In[19]:


#forms
import os
path = 'testSet/Stimuli/Action/'
picnames = os.listdir(path)
import ipywidgets as widgets
un = widgets.Text(
    value='',
    placeholder='Enter Username',
    description='Username:',
    disabled=False
)
display(un)

acc = widgets.Text(
    value='',
    placeholder='Enter Account',
    description='Account:',
    disabled=False
)
display(acc)


# In[22]:


from PIL import Image
import random
import numpy as np
#random images
i1 = path+picnames[random.randint(0,np.shape(picnames)[0])]
i2 = path+picnames[random.randint(0,np.shape(picnames)[0])]
i3 = path+picnames[random.randint(0,np.shape(picnames)[0])]
i4 = path+picnames[random.randint(0,np.shape(picnames)[0])]
#resize proportionally
width, height = Image.open(i1).size
r1 = Image.open(i1).resize((int(width/10), int(height/10)), Image.ANTIALIAS)
r2 = Image.open(i2).resize((int(width/10), int(height/10)), Image.ANTIALIAS)
r3 = Image.open(i3).resize((int(width/10), int(height/10)), Image.ANTIALIAS)
r4 = Image.open(i4).resize((int(width/10), int(height/10)), Image.ANTIALIAS)
display(r1)
display(r2)
display(r3)
display(r4)


# In[25]:


import string
#generate password from letters
password = ''.join(random.choices(string.ascii_uppercase, k=4))
print('your password is : ' + password)


# In[26]:


#generating log for convenience in testing
import pandas as pd
import time
ts = time.time()
import datetime
st = datetime.datetime.fromtimestamp(ts).strftime('%Y-%m-%d %H:%M:%S')
import pickle
save = pickle.load(open('save.p', 'rb'))
data = [un.value, acc.value, password, i1, i2, i3, i4, 0, 0, st]
data = np.reshape(data, (1,10))
log = pd.DataFrame(data, columns = ['username', 'account', 'password', 'i1', 'i2', 'i3', 'i4', 'attempts', 'correct', 'updated'])
log = log.append(save, ignore_index=True)
pickle.dump(log, open( "save.p", "wb" ) )
log


# In[27]:


#forms for testing
tun = widgets.Text(
    value='',
    placeholder='Enter Username',
    description='Username:',
    disabled=False
)
display(tun)

tacc = widgets.Text(
    value='',
    placeholder='Enter Account',
    description='Account:',
    disabled=False
)
display(tacc)


# In[29]:


#current in progress visible for convenience in testing
save = pickle.load(open('save.p', 'rb'))
df = save.loc[(save['username'] == tun.value) & (save['account'] == tacc.value)]
df


# In[30]:


#retrive associated images with account
if(np.shape(df)[0]>1):
    df = df.iloc[0]
    
r1 = Image.open(df['i1'].iloc[0]).resize((int(width/10), int(height/10)), Image.ANTIALIAS)
r2 = Image.open(df['i2'].iloc[0]).resize((int(width/10), int(height/10)), Image.ANTIALIAS)
r3 = Image.open(df['i3'].iloc[0]).resize((int(width/10), int(height/10)), Image.ANTIALIAS)
r4 = Image.open(df['i4'].iloc[0]).resize((int(width/10), int(height/10)), Image.ANTIALIAS)
display(r1)
display(r2)
display(r3)
display(r4)


# In[31]:


#form for password check
tpass = widgets.Text(
    value='',
    placeholder='Enter Password',
    description='Password:',
    disabled=False
)
display(tpass)


# In[35]:


#print message and mark attempts ie successes and failures
ts = time.time()
st = datetime.datetime.fromtimestamp(ts).strftime('%Y-%m-%d %H:%M:%S')
if(tpass.value == df['password'].iloc[0]):
    print('congrats you got it right')
    df['attempts'] = int(df['attempts'])+1
    df['correct'] = int(df['correct']) + 1
    df['updated'] = st
    save.loc[(save['username'] == tun.value) & (save['account'] == tacc.value)] = df
else:
    print('WRONG PASSWORD')
    df['attempts'] = int(df['attempts'])+1
    df['updated'] = st
    save.loc[(save['username'] == tun.value) & (save['account'] == tacc.value)] = df
    
pickle.dump(save, open( "save.p", "wb" ) )


# In[37]:


#log file
save

