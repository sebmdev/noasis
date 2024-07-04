import cookieParser from 'cookie-parser'
import dotenv from 'dotenv'
import express, { NextFunction, Request, Response } from 'express'
import session from 'express-session'
import mysql from 'mysql2/promise'
import Signup from './routes/Signup'
import Login from './routes/Login'
import CheckSession from './routes/CheckSession'
import Logout from './routes/Logout'
const MySQLStore = require('express-mysql-session')(session)

declare module 'express-session' {
  export interface SessionData {
    user: { [key: string]: any }
    val: string
  }
}

dotenv.config({ path: ['config/.env', `config/.env.${process.env.NODE_ENV}`] })

const app = express()
console.log(`Environment: ${process.env.NODE_ENV}`)
console.log(`MOTD: ${process.env.MESSAGE}`)

const options = {
  host: process.env.MYSQL_HOST,
  port: 3306,
  user: process.env.MYSQL_USER,
  password: process.env.MYSQL_PASSWORD,
  database: process.env.MYSQL_SCHEMA,
}

export const pool = mysql.createPool({
  ...options,
  connectionLimit: 10,
})

const sessionStore = new MySQLStore(options, pool)

// ROUTES SETUP
app.use(express.json())
app.use(cookieParser())
app.use(
  session({
    secret: process.env.SESSION_SECRET as string,
    resave: false,
    saveUninitialized: false,
    store: sessionStore,
    cookie: {
      secure: false,
      maxAge: 1000 * 60 * 60 * 24 * 365 * 5
    }
  })
)

app.get('/', async (req: Request, res: Response) => {
  if (!req.session.user) {
    req.session.user = {
      'email': 'sebm@gmail.com'
    }
    console.log('Created session: ', req.sessionID)
  } else {
    console.log('Loaded session: ', req.sessionID)
  }
  setTimeout(() => {res.json({message: "Hello world!"})}, 2000)
})

app.get('/check-session', (req: Request, res: Response) => CheckSession(req, res))

app.post('/signup', async (req: Request, res: Response) => Signup(req, res))

app.post('/login', async (req: Request, res: Response) => Login(req, res))

app.delete('/logout', async (req: Request, res: Response) => Logout(req, res))

app.get('/test', async (req: Request, res: Response) => {
  console.log(req.session.id)
  console.log(req.session.user)
  res.json({"user": req.session.user, "id": req.session.id})
})

app.listen(process.env.PORT, () =>
  console.log(`Listening on port ${process.env.PORT}`)
)