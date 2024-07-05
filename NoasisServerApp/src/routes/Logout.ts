import { Request, Response } from 'express'

export default async function Logout(req: Request, res: Response) {
  req.session.destroy((err) => {
    if (err) {
      return res.status(500).json({
        error: 'Could not log out: ' + err,
      })
    }
    // res.clearCookie('connect.sid', {
    //   secure: process.env.NODE_ENV === 'production', // false for development, true for production
    //   maxAge: 1000 * 60 * 60 * 24,
    //   sameSite: 'none',
    // })

    console.log(req.cookies)
    console.log("successfully logged out")
    return res.status(200).json({
      message: 'You have successfully logged out.',
    })
  })
}
