import { useState } from 'react';
import SignIn from './components/signIn';
import Admin from './components/admin';
import UserPage from './components/user/UserPage';
import 'react-widgets/styles.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import AdminDashboard from './components/AdminComponent/AdminDashboard/AdminDashboard';

function App() {
  var currentrole="login"
  if(sessionStorage.getItem("role")!=null)
  {
    currentrole=sessionStorage.getItem("role"); 
  }
  const [state, setState]  = useState(currentrole);
  const rolestate=(role)=>{
    setState(role);
  }
  return (
    <div className="App">
      {/* <AdminDashboard/> */}
     {state==="login"?<SignIn rolestate={rolestate}/>:(state==="admin"?<Admin rolestate={()=>{rolestate("admin")}}/>:<UserPage/>)}
    </div>
  );
}

export default App;
