import React from "react";
import { Link } from "react-router-dom";
import { Combobox } from "react-widgets";

export default function ExamTopicMappingForm(props) {
  return (
    <div>
      <form
        onSubmit={props.submitHandler}
        className="rounded-1 row mt-3 d-flex align-items-center justify-content-aroundmin-vw-100"
        style={{ background: "#D9EBFF" }}
        id="examtopping"
      >
        <div className="container">
          <div className="row ms-1 p-5">
            <div className="col-12 col-sm-6 row mt-3 d-flex align-items-center justify-content-center">
              <label
                htmlFor="examId"
                className="col-sm-2 mt-2"
                style={{ fontWeight: "bolder" }}
              >
                Exam Name
              </label>
              <div className="col col-sm-7">
                <Combobox
                name="examIDerr"
                  data={props.exams}
                  dataKey="examId"
                  textField="examName"
                  defaultValue={props.examId ? props.examId : "Choose ONE"}
                  onChange={(value) => props.handleSelectExamChange(value)}
                />
                <div className="invalid-feedback mx-sm-5" id="examIDerr">
                  Please Choose Exam Name
                </div>
              </div>
            </div>
            <div className="col-12 col-sm-6 row mt-3 d-flex align-items-center justify-content-center">
              <label
                htmlFor="expiratitopicIdonDate"
                className="col-sm-2 mt-2"
                style={{ fontWeight: "bolder" }}
              >
                Topic Name
              </label>
              <div className="col col-sm-7">
                <Combobox
                  data={props.topics}
                  dataKey="topicId"
                  textField="topicName"
                  defaultValue={props.topicId ? props.topicId : "Choose ONE"}
                  onChange={(value) => props.handleSelectTopicChange(value)}
                />
                <div className="invalid-feedback mx-sm-5" id="topicIDerr">
                  Please Choose Topic Name
                </div>
              </div>
            </div>
            <div className="col-12 col-sm-6 row mt-3 d-flex align-items-center justify-content-center">
              <label
                htmlFor="percentage"
                className="col-sm-2 mt-2"
                style={{ fontWeight: "bolder" }}
              >
                Percentage
              </label>
              <div className="col col-sm-7">
                <input
                  type="text"
                  name="percentage"
                  className="form-control mx-sm-5"
                  defaultValue={props.percentage}
                  onChange={(value)=>props.handleChangePercentage(value)}
                />
                <div className="invalid-feedback mx-sm-5" id="percentageerr">
                  Please Enter Percentage
                </div>
              </div>
            </div>
            <div className="col-12 col-sm-6 row mt-3 d-flex align-items-center justify-content-center">
              <label
                htmlFor="topicPassPercentage"
                className="col-sm-2 mt-2"
                style={{ fontWeight: "bolder" }}
              >
                Topic Pass Percentage
              </label>
              <div className="col col-sm-7">
                <input
                  type="text"
                  name="topicPassPercentage"
                  className="form-control mx-sm-5"
                  defaultValue={props.topicPassPercentage}
                />
                <div
                  className="invalid-feedback mx-sm-5"
                  id="topicpasspercentageerr"
                >
                  Please Enter Topic Pass Percentage
                </div>
              </div>
            </div>
            <div className="col-12 col-sm-6 row mt-3 d-flex align-items-center justify-content-center">
              <label
                htmlFor="questionsPerExam"
                className="col-sm-2 mt-2"
                style={{ fontWeight: "bolder" }}
              >
                Questions Per Exam
              </label>
              <div className="col col-sm-7">
                <input
                  type="text"
                  name="questionsPerExam"
                  className="form-control mx-sm-5"
                  defaultValue={props.selectedQuestionsPerExam}
                  onChange={(value) => props.handleSelectCountChange(value)}
                />
                <div
                  className="invalid-feedback mx-sm-5"
                  id="questionsperexamerr"
                >
                  Please Enter Questions Per Exam
                </div>
              </div>
            </div>
          </div>
        </div>
        <div className="container">
          <div className="row">
            <div
              className="mx-auto d-flex justify-content-center p-5"
              style={{ width: "500px" }}
            >
              <input
                type="submit"
                name="submit"
                value="CREATE"
                className="border-none px-3 py-1 mt-4 mb-2 text-white"
                style={{
                  fontWeight: "bolder",
                  background:
                    "radial-gradient(circle at 48.7% 44.3%, rgb(30, 144, 231) 0%, rgb(56, 113, 209) 22.9%, rgb(38, 76, 140) 76.7%, rgb(31, 63, 116) 100.2%)",
                }}
              />
              <Link
                exact
                to="/AdminDashboard/Exam"
                style={{
                  textDecoration: "none",
                  fontWeight: "bolder",
                  background:
                    "radial-gradient(circle at 48.7% 44.3%, rgb(30, 144, 231) 0%, rgb(56, 113, 209) 22.9%, rgb(38, 76, 140) 76.7%, rgb(31, 63, 116) 100.2%)",
                  padding: "9px",
                  border: "2px solid gray",
                  width: "0.9in",
                }}
                className="ms-3 mt-4 mb-2 text-white d-flex justify-content-center"
              >
                CLOSE
              </Link>
            </div>
          </div>
        </div>
      </form>
    </div>
  );
}
