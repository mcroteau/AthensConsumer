# README

athens stub consumer


/**Im going to rename all quartz job classes**/

                <%List<QuartzIngestLog> kronosIngestLogs = (ArrayList) request.getAttribute("kronosIngestLogs");%>
                <%for(QuartzIngestLog kronosIngestLog : kronosIngestLogs){%>
                    <tr>
                        <td><%=kronosIngestLog.getId()%></td>
                        <td>${kronosIngestLog.kdate}</td>
                        <td>${kronosIngestLog.kadtcnt}</td>
                        <td>${kronosIngestLog.kproc}</td>
                        <td>${kronosIngestLog.ktot}</td>
                        <td>${kronosIngestLog.kstatus}</td>
                        <%if(kronosIngestLog.getKstatus().equals("Running")){%>
                            <td class="running-job">
                                <a href="${pageContext.request.contextPath}/jobs" class="btn btn-default">View Running Ingest</a>
                            </td>
                        <%}else{%>
                            <td>--</td>
                        <%}%>
                    </tr>
                <%}%>