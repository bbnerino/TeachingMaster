import {useState} from 'react'
import { Box } from '@chakra-ui/react'
import './scss/Header.scss'
import Sidebar from './Sidebar'
import { Link, useHistory } from 'react-router-dom'

const Header = ({is_login,setIs_Login,user,setUser}) => {
  const history = useHistory()
  const logout = ()=>{
    localStorage.clear()
    setUser("")
    setIs_Login(false)
    history.push('/')
  }
  return (
    <div>
      <Box className='header-box' 
      w='100%' p={4} color='white'>
        {is_login && <Sidebar user={user} className='header-left'/>}
        <Link className='header-title' to ="/">
          Teaching Master
        </Link>  
        <div className='header-right'>
          {is_login? <div onClick={logout}>로그아웃</div>:
            <div>
              <Link to="/login">로그인</Link>
              <Link to="/signup">회원가입</Link>
            </div>}
        </div>
      </Box>    
    </div>
  )
}

export default Header