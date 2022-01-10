using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using MVCModelEx.Models;


namespace MVCDemoEx.Controllers
{
    public class HomeController : Controller
    {
        // GET: Home
        public ActionResult IndexHTMLModel()
        {
            return View();
        }
        public ActionResult IndexHTMLModel(int txtAVal,int txtBVal,string Operation)
        {
            int Res = 0;
            if (Operation == "Addition")
            {
                Res = txtAVal + txtBVal;
            }
            else if (Operation == "Subtraction")
            {
                Res = txtAVal - txtBVal;
            }
            ViewBag.ResultHtml = Res;
            return View();
        }
        public ActionResult IndexHTMLModel()
        {
            Inputscls Obj = new Inputscls();
            Obj.FirstVal = 1001;
            Obj.SecondVal = 1002;
            return View(Obj);
        }
        public ActionResult IndexHTMLModel(InputCls)
    }
}